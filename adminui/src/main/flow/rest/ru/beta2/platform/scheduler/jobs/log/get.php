<?php

$executions = \covers::scheduler()->getJobExecutionLog(b2flow()->jobKey ?: null, 500);

return \b2\tpl\xml::render(['executions'=>$executions]);