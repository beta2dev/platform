<?php

return [
    'restDir' => [
        '/b2x/admin' => b2xhome('flow/rest'), // only admin resources accessible
        '*' => b2root('flow/rest')
    ],
    'varPath' => [
        '/ru/beta2/platform/core/config/{name}',
        '/ru/beta2/platform/core/config/{name}/_ui',
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}',
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}/_ui',
    ]
];