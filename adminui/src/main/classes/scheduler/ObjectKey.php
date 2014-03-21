<?php

namespace scheduler;

/**
 * User: inc
 * Date: 20.03.14
 * Time: 19:18
 */
class ObjectKey
{

    var $name;
    var $group;

    function __construct($name = null, $group = null)
    {
        $this->name = $name;
        $this->group = $group;
    }

}
