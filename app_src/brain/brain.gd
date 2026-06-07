extends Node


const ENDPOINT = "http://127.0.0.1:5001/api/v1/generate"
const TIMEOUT = 3.0


func talk(msg: String) -> String:
	var http_client = HTTPRequest.new()
	http_client.timeout = self.TIMEOUT
	Engine.get_main_loop().current_scene.add_child(http_client)
	
	var headers = ["Content-Type: application/json"]
	var body_dict = {
		"prompt": msg,
		"max_length": 50,
		"temperature": 0.7,
		"seed": 1234,
		"grammar": "root ::= [a-zA-Z]+" 
	}
	var body_json = JSON.stringify(body_dict)
	print("brain_send " + body_json)
	
	var error = http_client.request(self.ENDPOINT, headers, HTTPClient.METHOD_POST, body_json)
	
	if error != OK:
		printerr("brain_error_connect1 " + str(error))
		http_client.queue_free()
		return "Error: Could not initiate HTTP request."
		
	var response = await http_client.request_completed
	
	http_client.queue_free()
	
	var response_code = response[1]
	var response_body = response[3].get_string_from_utf8()
	print("brain_got " + str(response_code) + ", " + str(response_body))
	
	if response_code != 200:
		printerr("brain_error_connect2 " + str(response_code))
		return "Error: Server returned status code " + str(response_code)
		
	var json_data = JSON.parse_string(response_body)
	var answer = ""
	if json_data and json_data.has("results") and json_data["results"].size() > 0:
		answer = json_data["results"][0].get("text", "").strip_edges()
		print("brain_parsed_answer")
		return answer
		
	printerr("brain_error_parse " + str(json_data))
	return "Error: Unexpected response format structure."
