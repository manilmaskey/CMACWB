<?php

$python_code = $_POST['code'];
$fileName = $_POST['filename'];


$lines = explode("\n", $python_code);
$new_wrapped_code = "";


$global_variables_declaration = "";
$global_variables_use = "";

$global_variable_list = array();

foreach ($lines as $line) {
    $new_wrapped_code .= " " . $line . "\n";

    if (strpos($line, "global") !== FALSE) {
        $variable_name = str_replace("global ", "", trim($line));
        $global_variable_list[] = $variable_name;

        $global_variables_declaration .= $variable_name . " = 0" . "\n";
        $global_variables_use .= " global " . $variable_name . "" . "\n";

    }

}

$wrapped_python_code = "import trace_execution\n" . "trace_execution.trace()\n" . $global_variables_declaration . "\n" . "def wrapper():\n" . $global_variables_use . "\n" . $new_wrapped_code . "\nwrapper()\ntrace_execution.stop_trace()\ntrace_execution.generatePROV(\"" . $fileName . "\",trace_execution.input_output_files)";

echo $wrapped_python_code;