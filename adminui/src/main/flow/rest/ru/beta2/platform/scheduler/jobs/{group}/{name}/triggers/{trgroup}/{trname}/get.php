<?php

$trigger = covers::scheduler()->getTrigger(scheduler::triggerKey());

return \b2\tpl\xml::render(['trigger'=>$trigger]);