<?php

$triggers = \covers::scheduler()->getJobTriggers(\scheduler::jobKey());

foreach ($triggers as $v) {
    $v->misfireInstructionTitle = \scheduler::$MISFIRE_MAP[$v->misfireInstruction];
}

return \b2\tpl\xml::render(['triggers'=>$triggers]);