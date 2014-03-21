<?php
/**
 * User: inc
 * Date: 20.03.14
 * Time: 21:13
 */
class scheduler
{

    static $MISFIRE_MAP = [];

    // todo !!! implement delete jobs
    // todo !!! implement view trigger log for job

    static function createJobDescriptorFromInput()
    {
        $obj = b2input()->json();
        $job = new \scheduler\JobDescriptor();

        $job->group = trim($obj->group) ?: b2config()->scheduler['defaultGroupName'];
        $job->name = $obj->name;
        $job->type = $obj->type;
        $job->description = $obj->description;

        if ($obj->data) {
            $job->data = parse_ini_string($obj->data);
        }
        if (empty($job->data)) {
            $job->data = null;
        }

//        var_dump($job);

        return $job;
    }

    static function createTriggerDescriptorFromInput()
    {
        $obj = b2input()->json();
        $tr = new \scheduler\TriggerDescriptor();

        $tr->group = trim($obj->group) ?: b2config()->platform['scheduler']['defaultGroupName'];
        $tr->name = $obj->name;
        $tr->description = $obj->description;
        $tr->priority = $obj->priority ? intval($obj->priority) : b2config()->platform['scheduler']['defaultTriggerPriority'];
        $tr->misfireInstruction = $obj->misfireInstruction ? intval($obj->misfireInstruction) : 0;
        $tr->schedule = $obj->schedule;

//        var_dump($tr);

        if ($obj->startTime) {
            $tr->startTime = new \DateTime($obj->startTime);
        }
        if ($obj->endTime) {
            $tr->endTime = new \DateTime($obj->endTime);
        }

        return $tr;
    }

    static function jobKey()
    {
        return new \scheduler\ObjectKey(b2input()->name, b2input()->group);
    }

    static function triggerKey()
    {
        return new \scheduler\ObjectKey(b2input()->trname, b2input()->trgroup);
    }
}

scheduler::$MISFIRE_MAP = [
    0 => 'SMART_POLICY',
    -1 => 'IGNORE_MISFIRE_POLICY',
    1 => 'FIRE_ONCE_NOW',
    2 => 'DO_NOTHING',
];
