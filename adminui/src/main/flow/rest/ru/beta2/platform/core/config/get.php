<?php

$configNames = \covers::config()->getConfigNames();

return \b2\tpl\xml::render(['configNames'=>$configNames]);
