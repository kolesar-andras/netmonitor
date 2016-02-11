#!/usr/bin/env php
<?php

/**
 * Read netmonitor pages from gammu
 *
 * create a batch file for gammu
 * log system time for georeferencing later
 * log phone time (pi does not have RTC)
 * run forever
 *
 * @author Kolesár András <kolesar@kolesar.hu>
 * @since 2015.04.04
 */

$gammu = 'gammu-1.35.0/build-configure/gammu/gammu';
if (count($argv>1)) foreach (array_slice($argv, 1) as $arg)
	$gammu .= ' ' . $arg;

$tests = array(01, 02, 03, 04, 05, 07, 10, 11, 20, 23);
$output = '';
foreach ($tests as $test) {
	$output .= sprintf("nokianetmonitor %02d\n", $test);
}
file_put_contents('batch', $output);

$tc = 0;
echo "-- startup\n";
while (true) {
	echo sprintf("[%s]\n", date('Y-m-d H:i:s'));
	if (!($tc++ % 60)) {
		$cmd = sprintf('%s getdatetime', $gammu);
		passthru($cmd);
	}
	$cmd = sprintf('%s batch batch', $gammu);
	passthru($cmd);
}
