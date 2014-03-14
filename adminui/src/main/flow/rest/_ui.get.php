<?php

$availableModules = [
    'ru.beta2.platform.core.config.ConfigServiceCover' => ['name'=>'ru.beta2.platform.core.config', 'title'=>'Конфигурация'],
    'ru.beta2.platform.scheduler.SchedulerCover' => ['name'=>'ru.beta2.platform.scheduler.jobs', 'title'=>'Планировщик'],
];

$coversData = file_get_contents(b2config()->platform['undercoverUrl']);

$covers = array_map(function($v) {return explode(':', trim($v));}, explode("\n", trim($coversData)));

foreach ($covers as $v) {
    if ($m = $availableModules[$v[1]]) {
        $modules[] = $m;
    }
//    $modules[] = ['name'=>$v[0], 'title'=>$v[1]];
}

$modules[] = ['name'=>'b2x.admin.modules', 'title'=>'Администрирование'];

return \b2\tpl\xml::render(['modules'=>$modules]);
