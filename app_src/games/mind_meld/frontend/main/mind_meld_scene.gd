extends BaseGameScene

@onready var chat_input: LineEdit = $MainVBox/InputHBox/ChatInput

signal quit_requested()


# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	chat_input.grab_focus()
	var mind_meld_manager := self.game_manager_instance as MindMeldManager
	mind_meld_manager.game_state_updated.connect(_on_game_state_updated)


func _on_quit_button_pressed() -> void:
	var mind_meld_manager := self.game_manager_instance as MindMeldManager
	mind_meld_manager.quit_game()
	quit_requested.emit()


func _on_chat_input_text_submitted(new_text: String) -> void:
	_on_new_message()


func _on_send_button_pressed() -> void:
	_on_new_message()


func _on_new_message() -> void:
	print("mm_scn_new_msg")
	var chat_text = $MainVBox/InputHBox/ChatInput.text
	chat_input.clear()
	var mind_meld_manager := self.game_manager_instance as MindMeldManager
	var mgr_turn_result = await mind_meld_manager.on_new_user_word(chat_text)
	if !mgr_turn_result.error_text.is_empty():
		print("mm_scn_new_msg_error")
		var error_label := Label.new()
		error_label.add_theme_color_override("font_color", Color.RED)
		error_label.text = "Error: " + mgr_turn_result.error_text
		$MainVBox/ChatScroll/ChatHistory.add_child(error_label)


func _on_game_state_updated(chat_msgs: Array[Label]) -> void:
	var chat_scroll := $MainVBox/ChatScroll
	var chat_history := $MainVBox/ChatScroll/ChatHistory
	var scrollbar: VScrollBar = chat_scroll.get_v_scroll_bar()
	
	if not scrollbar.changed.is_connected(_on_scrollbar_changed):
		scrollbar.changed.connect(_on_scrollbar_changed.bind(chat_scroll, scrollbar))
	
	for child in chat_history.get_children():
		child.free()
	
	for label in chat_msgs:
		chat_history.add_child(label)


func _on_scrollbar_changed(scroll_container: ScrollContainer, scrollbar: VScrollBar) -> void:
	scroll_container.scroll_vertical = int(scrollbar.max_value)
	
	if scrollbar.changed.is_connected(_on_scrollbar_changed):
		scrollbar.changed.disconnect(_on_scrollbar_changed)
