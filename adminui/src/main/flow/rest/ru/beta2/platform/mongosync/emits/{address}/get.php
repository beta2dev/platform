<?php

$emit = \covers::mongosyncEmitManager()->getEmit(b2input()->address);
sort($emit->namespaces);

return \b2\tpl\xml::render([
    'emit' => $emit
]);