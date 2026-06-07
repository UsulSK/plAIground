class_name Guess
extends RefCounted


var player: Player
var word: String


func _to_string() -> String:
	return "[guess " + str(self.player) + ": " + self.word + "]"
