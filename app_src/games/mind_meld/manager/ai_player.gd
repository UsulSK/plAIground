class_name AiPlayer
extends RefCounted


static func make_guess(state: MindMeltState) -> String:
	var ai_prompt = _create_prompt(state)
	var ai_word_response = await Brain.talk(ai_prompt)
	var clean_word = ai_word_response.strip_edges()
	print("ai_answer " + clean_word)
	return clean_word


static func _create_prompt(state: MindMeltState) -> String:
	# 1. Llama 3 System Header
	var system_rules = "<|begin_of_text|><|start_header_id|>system<|end_header_id|>\n\n"
	system_rules += "You are a cooperative AI playing a word game called 'Mind Melt'.\n"
	system_rules += "RULES:\n"
	system_rules += "- Every round, players simultaneously say one word.\n"
	system_rules += "- The goal is for everyone to eventually say the exact same word.\n"
	system_rules += "- To win, your word must compromise, find common ground, or be 'in between' the words said in the most recent round.\n"
	system_rules += "- CRITICAL: You are strictly forbidden from reusing ANY word that has already been said in the game history.\n"
	system_rules += "- Respond with EXACTLY ONE SINGLE WORD. No punctuation, no explanations, no filler text.<|eot_id|>"
	
	# 2. Llama 3 User Header
	var user_turn = "<|start_header_id|>user<|end_header_id|>\n\n"
	
	var finalized_turns: Array[Turn] = []
	var total_players_count = state.players.size()
	
	for turn in state.turns:
		# A turn is only finalized if EVERY player has already submitted their guess for it.
		# If it has fewer guesses than players, it is the active simultaneous turn!
		if turn.guesses.size() == total_players_count:
			finalized_turns.append(turn)
			
	var completed_turns_count = finalized_turns.size()
	var history_text = ""
	var clue_text = ""
	
	if completed_turns_count == 0:
		# TRUE FIRST ROUND SETUP: No complete rounds exist yet. No forbidden text blocks.
		clue_text = "This is round 1. Think of a common, broad starter noun that serves as a good entry point for your teammate.\n"
	else:
		# LATER ROUNDS SETUP: Extract history exclusively from verified completed rounds
		var forbidden_words: Array[String] = []
		for turn in finalized_turns:
			for guess in turn.guesses:
				var normalized_word = guess.word.strip_edges().to_lower()
				if not forbidden_words.has(normalized_word):
					forbidden_words.append(normalized_word)
		
		# Compile history text blocks
		history_text = "FORBIDDEN WORDS (Do not use these!):\n"
		history_text += ", ".join(forbidden_words) + "\n\n"
		
		# Clues are extracted strictly from the last genuinely finalized turn
		var last_completed_turn = finalized_turns[-1]
		var last_words: Array[String] = []
		for guess in last_completed_turn.guesses:
			last_words.append(guess.word.strip_edges())
			
		clue_text = "LAST ROUND'S WORDS:\n"
		clue_text += ", ".join(last_words) + "\n"
		clue_text += "Your task: Think of a single word that is a logical conceptual bridge or 'in between' those words.\n"
		
	# 3. Llama 3 Assistant Header
	var assistant_header = "<|eot_id|><|start_header_id|>assistant<|end_header_id|>\n\n"
	
	var final_prompt = system_rules + user_turn + history_text + clue_text + assistant_header
	return final_prompt
