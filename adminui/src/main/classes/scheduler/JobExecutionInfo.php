<?php

namespace scheduler;

/**
 * User: inc
 * Date: 22.03.14
 * Time: 19:42
 */
class JobExecutionInfo
{
    var $jobKey;
    var $triggerKey;

    var $fireTime;
    var $scheduledFireTime;
    var $jobRunTime;
    var $refireCount;

    var $fireInstanceId;
    var $exception;
}
