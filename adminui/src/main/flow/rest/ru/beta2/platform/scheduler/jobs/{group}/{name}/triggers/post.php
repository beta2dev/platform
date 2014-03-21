<?php

\covers::scheduler()->addTrigger(
    \scheduler::jobKey(),
    \scheduler::createTriggerDescriptorFromInput());


