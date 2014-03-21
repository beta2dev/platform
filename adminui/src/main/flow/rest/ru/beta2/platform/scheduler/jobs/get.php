<?php

$jobs = \covers::scheduler()->getJobKeys();

usort($jobs, function($a, $b) {
    return strcmp($a->group, $b->group) ?: strcmp($a->name, $b->name);
});

return \b2\tpl\xml::render(['jobs'=>$jobs]);