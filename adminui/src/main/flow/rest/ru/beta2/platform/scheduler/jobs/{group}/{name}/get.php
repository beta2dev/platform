<?php

$job = \covers::scheduler()->getJob(\scheduler::jobKey());

$dataText = '';
if (!empty($job->data)) {
    foreach ($job->data as $k=>$v) {
        if ($k != 'durability') {
            $dataText .= "$k=$v\n";
        }
    }
}

return \b2\tpl\xml::render(['job'=>$job, 'dataText'=>$dataText]);