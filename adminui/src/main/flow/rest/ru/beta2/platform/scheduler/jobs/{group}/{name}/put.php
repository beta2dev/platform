<?php

covers::scheduler()->updateJob(
    scheduler::jobKey(),
    scheduler::createJobDescriptorFromInput());


