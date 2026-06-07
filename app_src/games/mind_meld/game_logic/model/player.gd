class_name Player
extends RefCounted


var name: String


func _to_string() -> String:
	return "[player " + self.name + "]"
