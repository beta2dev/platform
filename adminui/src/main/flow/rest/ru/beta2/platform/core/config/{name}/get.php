<?php

$name = b2input()->name;
$value = \covers::config()->getConfigValue($name);

echo "<config name=\"$name\"><![CDATA[$value]]></config>";