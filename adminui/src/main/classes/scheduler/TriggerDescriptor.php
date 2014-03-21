<?php

namespace scheduler;

/**
 * User: inc
 * Date: 21.03.14
 * Time: 12:23
 */
class TriggerDescriptor
{
    var $name;
    var $group;

    var $schedule;

    var $priority;
    var $misfireInstruction;

    var $description;

    var $startTime;
    var $endTime;
}
