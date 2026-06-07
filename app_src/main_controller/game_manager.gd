extends Node

var _gameManagers: Dictionary[String, Game] = {}
var _current_game: String = ""
const SCREEN_PERCENTAGE = 0.8


func _init() -> void:
	self._add_game(MindMeldManager.new())


func _add_game(game: Game) -> void:
	self._gameManagers[game.get_game_name()] = game


func _ready() -> void:
	self.get_tree().node_added.connect(_on_node_added)


func _on_node_added(node: Node) -> void:
	if node.get_parent() != get_tree().root:
		return
		
	if node is BaseGameScene and self._current_game != "":
		var specific_game_manager: Game = self._gameManagers[self._current_game]
		node.game_manager_instance = specific_game_manager

	if node.has_signal("quit_requested"):
		if node.quit_requested.is_connected(_on_game_quit_requested):
			node.quit_requested.disconnect(_on_game_quit_requested)
		node.quit_requested.connect(_on_game_quit_requested)


func _on_game_quit_requested() -> void:
	self._current_game = ""
	self.get_tree().change_scene_to_file("res://main_menu/main_menu.tscn")


func get_game_names() -> Array[String]:
	var game_names: Array[String] = []

	for game_key in self._gameManagers:
		var game = self._gameManagers[game_key]
		var game_name: String = game.get_game_name()
		game_names.append(game_name)

	return game_names


func start_game(game_name: String) -> void:
	var scene_path = self._gameManagers[game_name].get_game_scene()
	self._current_game = game_name
	self.get_tree().change_scene_to_file(scene_path)
