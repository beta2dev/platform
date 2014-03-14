<?php

//namespace ru\beta2\platform\scheduler;

class JobDescriptor
{
    var $group;
    var $name;
    var $type;
    var $description;
    var $data;
}

$obj = b2input()->json();
$job = new JobDescriptor();

$job->group = trim($obj->group) ?: 'user';
$job->name = $obj->name;
$job->type = $obj->type;
$job->description = $obj->description;

if ($obj->data) {
    $job->data = parse_ini_string($job->data);
}
if (empty($job->data)) {
    $job->data = null;
}

$jobs = \covers::scheduler()->addJob($job);


