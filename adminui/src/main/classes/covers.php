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
//            $options->typeMap['ru\beta2\platform\scheduler\JobDescriptor'] = 'ru.beta2.platform.scheduler.JobDescriptor';
            $options->typeMap['JobDescriptor'] = 'ru.beta2.platform.scheduler.JobDescriptor';
            $options->before = function($ctx) {var_dump($ctx->payload);};
            $scheduler = self::createCoverProxy('scheduler', $options);
        }
        return $scheduler;
    }

    private static function createCoverProxy($path, $options = null)
    {
        return new \HessianClient(\b2\util\Path::concat(b2config()->platform['undercoverUrl'], $path), $options);
    }

}