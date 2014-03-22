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
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}/log',
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}/log/_ui',
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}/triggers',
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}/triggers/_ui',
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}/triggers/{trgroup}/{trname}',
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}/triggers/{trgroup}/{trname}/_ui',
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}/triggers/{trgroup}/{trname}/pause',
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}/triggers/{trgroup}/{trname}/resume',
        '/ru/beta2/platform/scheduler/jobs/{group}/{name}/triggers/{trgroup}/{trname}/delete', // todo DEFFERED переделать на HTTP DELETE
    ]
];