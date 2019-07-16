#!/bin/bash -eu

SCRIPT_DIR=`cd "$(dirname $0)"; pwd -P`
echo "SCRIPT_DIR=$SCRIPT_DIR"

TEST_DIR=`cd ..; pwd -P`
echo "TEST_DIR=$TEST_DIR"

PROJECT_DIR=`cd ../../..; pwd -P`
echo "PROJECT_DIR=$PROJECT_DIR"

OUTPUT_DIR="$SCRIPT_DIR/output"
echo -e "OUTPUT_DIR=$OUTPUT_DIR\n"

function compare_results {
	TEST_NAME="$1"
	EXPECTED="$2"
	RESULTS="$3"

	DIFF_FILE="$OUTPUT_DIR/test_$TEST_NAME.diff.txt"
	echo "Test $TEST_NAME: Compare results, creating diff file '$DIFF_FILE'"

	if diff <(sort $EXPECTED) <(sort $RESULTS) > $DIFF_FILE
	then
		echo -e "Test $TEST_NAME: OK\n"
	else
		echo -e "Test $TEST_NAME: ERROR\n"
		exit 1;
	fi
}

function test {
	if [[ $1 != "Y" && $1 != "X" ]]; then
	echo "Please, enter the argument: X, if you need to test X-STR comparison; Y, if you need to test Y-STR comparison."
	else
	echo "Test: $1-STR comparison of two persons";
	BAM1="$TEST_DIR/resources/genome/compare/$1test1.bam"
	BAM2="$TEST_DIR/resources/genome/compare/$1test2.bam"
	BED="$TEST_DIR/resources/genome/compare/$1marker.bed"
	
	ACTUAL="$OUTPUT_DIR/test.$1_str_comparison.txt"
	EXPECTED="$SCRIPT_DIR/expected/expected_$1_str_comparison.txt"
	
	java -jar $PROJECT_DIR/build/libs/bio_relatives-all-1.4-SNAPSHOT.jar --compare2 $BAM1 $BAM2 $BED -th 4 -m XY > $ACTUAL
	compare_results $1 $EXPECTED $ACTUAL
	fi
}

test $1