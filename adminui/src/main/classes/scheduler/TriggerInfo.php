<?php

namespace scheduler;

/**
 * User: inc
 * Date: 21.03.14
 * Time: 21:57
 */
class TriggerInfo extends TriggerDescriptor
{
    var $nextFireTime;
    var $previousFireTime;
    var $finalFireTime;

    var $state;
}
