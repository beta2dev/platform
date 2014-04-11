<?php

$json = b2input()->json();

\covers::mongosyncEmitManager()->changeEmit($json->address, $json->namespaces);