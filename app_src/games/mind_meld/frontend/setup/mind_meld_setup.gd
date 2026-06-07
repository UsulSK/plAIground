extends BaseGameScene

@onready var player_slider: HSlider = $CenterContainer/VBoxContainer/HBoxContainer/PlayerSlider
@onready var count_label: Label = $CenterContainer/VBoxContainer/HBoxContainer/CountLabel
@onready var ok_button: Button = $CenterContainer/VBoxContainer/OkButton


func _ready() -> void:
	player_slider.value_changed.connect(_on_slider_value_changed)
	ok_button.pressed.connect(_on_ok_button_pressed)
	count_label.text = str(int(player_slider.value))


func _on_slider_value_changed(new_value: float) -> void:
	count_label.text = str(int(new_value))


func _on_ok_button_pressed() -> void:
	var total_players: int = int(player_slider.value)
	print("mm_setup_ok: " + str(total_players))
	var mind_meld_manager := self.game_manager_instance as MindMeldManager
	mind_meld_manager.start_game(total_players)
	self.get_tree().change_scene_to_file("res://games/mind_meld/frontend/main/mind_meld_scene.tscn")	
