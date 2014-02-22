<?php

$v = b2input()->json();
\covers::config()->setConfigValue($v->name, $v->value);