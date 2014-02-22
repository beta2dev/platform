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

    private static function createCoverProxy($path)
    {
        return new \HessianClient(\b2\util\Path::concat(b2config()->platform['undercoverUrl'], $path));
    }

}