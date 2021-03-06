<?php

set_include_path(get_include_path() . ';' . b2config()->platform['hessianIncludeDir']);
include_once 'HessianClient.php';

class covers
{

    static function config()
    {
        static $cover;
        if (!$cover) {
            $cover = self::createCoverProxy('config');
        }
        return $cover;
    }

    static function scheduler()
    {
        static $scheduler;
        if (!$scheduler) {
            $options = null;
            $options = new \HessianOptions();
            $options->typeMap['scheduler\ObjectKey'] = 'ru.beta2.platform.scheduler.ObjectKey';
            $options->typeMap['scheduler\JobDescriptor'] = 'ru.beta2.platform.scheduler.JobDescriptor';
            $options->typeMap['scheduler\TriggerDescritpr'] = 'ru.beta2.platform.scheduler.TriggerDescriptor';
            $options->typeMap['scheduler\TriggerInfo'] = 'ru.beta2.platform.scheduler.TriggerInfo';
            $options->typeMap['scheduler\JobExecutionInfo'] = 'ru.beta2.platform.scheduler.JobExecutionInfo';
//            $options->before = function($ctx) {var_dump($ctx->payload);};
//            $options->after = function($ctx) {var_dump($ctx->payload);};
            $scheduler = self::createCoverProxy('scheduler', $options);
        }
        return $scheduler;
    }

    static function mongosyncEmitManager()
    {
        static $mongosyncEmitManager;
        if (!$mongosyncEmitManager) {
            $mongosyncEmitManager = self::createCoverProxy('mongosync-emitter-emitmanager');
        }
        return $mongosyncEmitManager;
    }

    private static function createCoverProxy($path, $options = null)
    {
        return new \HessianClient(\b2\util\Path::concat(b2config()->platform['undercoverUrl'], $path), $options);
    }

}