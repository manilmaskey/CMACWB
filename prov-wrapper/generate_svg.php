<?php

function secure_tmpname($postfix = '.tmp', $prefix = 'tmp', $dir = null)
{
    // validate arguments
    if (!(isset($postfix) && is_string($postfix))) {
        return false;
    }
    if (!(isset($prefix) && is_string($prefix))) {
        return false;
    }
    if (!isset($dir)) {
        $dir = getcwd();
    }

    // find a temporary name
    $tries = 1;
    do {
        // get a known, unique temporary file name
        $sysFileName = tempnam($dir, $prefix);
        if ($sysFileName === false) {
            return false;
        }

        // tack on the extension
        $newFileName = $sysFileName . $postfix;
        if ($sysFileName == $newFileName) {
            return $sysFileName;
        }

        $newFileName = str_replace(".tmp", "", $newFileName);

        // move or point the created temporary file to the new filename
        // NOTE: these fail if the new file name exist
        $newFileCreated = (isWindows() ? @rename($sysFileName, $newFileName) : @link($sysFileName, $newFileName));
        if ($newFileCreated) {
            return $newFileName;
        }

        unlink($sysFileName);
        $tries++;
    } while ($tries <= 5);

    return false;
}

function isWindows()
{


    $os = php_uname('s');


    if (strpos($os, "Windows") !== FALSE) {
        return true;
    } else {
        return false;
    }

}

$tmp_dir = sys_get_temp_dir();
//$filename_provn=tempnam($tmp_dir,"prov");
$filename_svg = secure_tmpname(".svg", "prov", $tmp_dir);
$filename_provn = secure_tmpname(".provn", "prov", $tmp_dir);


$handle = fopen($filename_provn, "w");


$provn = $_POST['provn'];
fwrite($handle, $provn);


$os = php_uname('s');


if (strpos($os, "Windows") !== FALSE) {
    $prov_toolkit_executable = "cmd /c start C:/Users/akulkarni/Desktop/CMAC/provToolbox/bin/provconvert.bat";
} else {
    $prov_toolkit_executable = "/home/ubuntu/provToolbox/bin/provconvert";
}


$result = exec($prov_toolkit_executable . ' -' . 'infile ' . $filename_provn . ' -' . 'outfile ' . $filename_svg);


readfile($filename_svg);
fclose($handle);
