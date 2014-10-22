<?php

$tmp_dir=sys_get_temp_dir();
$filename_inputs_outputs=tempnam($tmp_dir,"prov");
$filename_provn=tempnam($tmp_dir,"prov");


$handle = fopen($filename_inputs_outputs, "w");



$scriptname =strip_tags( $_POST['scriptname']);

$_POST['inputs_outputs']=strip_tags($_POST['inputs_outputs']);
$_POST['inputs_outputs']=str_replace("{","",$_POST['inputs_outputs']);
$_POST['inputs_outputs']=str_replace("}","",$_POST['inputs_outputs']);

$inputs_outputs=explode(",",$_POST['inputs_outputs']);



 foreach($inputs_outputs as $i_o){
 	 $i_o=str_replace("http:", "http^", $i_o);
     $key_value=explode(":",$i_o);
     $key=trim($key_value[0]);
     $value=trim($key_value[1]);
	 $key=str_replace("http^", "http:", $key);
     fwrite($handle,  "$key^$value\n");
}


$os=php_uname('s');

if(strpos($os,"Windows")!==FALSE){
    $python_executable="python.exe";
}else{
    $python_executable="python";
}


$result = exec($python_executable.' generate_prov.py -i '.$filename_inputs_outputs ." -o ".$filename_provn. " -s ".$scriptname );
file_put_contents('/tmp/fio.txt', file_get_contents($filename_inputs_outputs));
readfile($filename_provn);
fclose($handle);
