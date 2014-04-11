<?php

$emits = \covers::mongosyncEmitManager()->getEmits();

if ($emits) foreach ($emits as $emit) {
    sort($emit->namespaces);
    $emit->namespaces = implode(', ', $emit->namespaces);
}

return \b2\tpl\xml::render([
    'emits' => $emits
]);