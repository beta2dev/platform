<?php

$jobTypes = \covers::scheduler()->getAvailableJobTypes();

$jt = [];
foreach ($jobTypes as $v) {
    $i = strrpos($v, '.');
    $jt[] = ['type'=>$v, 'title'=> $i != -1 ? substr($v, $i+1) : $v];
}

return \b2\tpl\xml::render(['jobTypes' => $jt]);