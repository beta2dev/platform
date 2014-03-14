<?php

$jobs = \covers::scheduler()->getJobKeys();

return \b2\tpl\xml::render(['jobs'=>$jobs]);