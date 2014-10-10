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
$filename_png = secure_tmpname(".png", "prov", $tmp_dir);


$handle = fopen($filename_svg, "w");


$svg = $_POST['svg'];
fwrite($handle, $svg);


$os = php_uname('s');


if (strpos($os, "Windows") !== FALSE) {
    $inkscape_executable = "cmd /c start execute_inkskape.bat";
    $args = "";
} else {
    $inkscape_executable = "inkscape";
    $args = " -z -e " . $filename_png . " -w 800 -h 800 " . $filename_svg;
}


$result = exec($inkscape_executable . $args);


header('Content-Type: image/png');
header('Content-Length: ' . filesize($filename_png));


readfile($filename_png);

fclose($handle);


foreach (glob("/tmp/prov*") as $filename) {
    unlink($filename);
}