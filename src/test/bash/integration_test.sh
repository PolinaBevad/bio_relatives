#!/bin/bash -eu
set -o pipefail

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

function test_1 {
	echo "Test 1: Comparison of two genomes of not parent and child"
	BAM1="$TEST_DIR/resources/genome/compare/testDad4.bam"
	BAM2="$TEST_DIR/resources/genome/compare/testMother4.bam"
	BED="$TEST_DIR/resources/genome/compare/correct.bed"
	
	ACTUAL="$OUTPUT_DIR/test_1.comparison_two_genomes_of_not_parent_and_child.txt"
	EXPECTED="$SCRIPT_DIR/expected/expected_two_genomes_not_parent_and_child.txt"
	
	java -jar $PROJECT_DIR/build/libs/bio_relatives-all-1.3-SNAPSHOT.jar --compare2 $BAM1 $BAM2 $BED -th 4 > $ACTUAL
	sed -i '1,8d' $ACTUAL
	compare_results 1 $EXPECTED $ACTUAL
}

function test_2 {
	echo "Test 2: Comparison of two genomes of parent and child"
	BAM1="$TEST_DIR/resources/genome/compare/testDad4.bam"
	BAM2="$TEST_DIR/resources/genome/compare/testSon4.bam"
	BED="$TEST_DIR/resources/genome/compare/correct.bed"
	
	ACTUAL="$OUTPUT_DIR/test_2.comparison_two_genomes_of_parent_and_child.txt"
	EXPECTED="$SCRIPT_DIR/expected/expected_two_genomes_parent_and_child.txt"
	
	java -jar $PROJECT_DIR/build/libs/bio_relatives-all-1.3-SNAPSHOT.jar --compare2 $BAM1 $BAM2 $BED -th 4 > $ACTUAL
	sed -i '1,8d' $ACTUAL
	compare_results 2 $EXPECTED $ACTUAL
}

function test_3 {
	echo "Test 3: Comparison of the genomes of three persons in the mitochondrial chromosome"
	BAM1="$TEST_DIR/resources/genome/compare/testDadMT.bam"
	BAM2="$TEST_DIR/resources/genome/compare/testMotherMT.bam"
	BAM3="$TEST_DIR/resources/genome/compare/testSonMT.bam"
	BED="$TEST_DIR/resources/genome/compare/correct2.bed"
	
	ACTUAL="$OUTPUT_DIR/test_3.comparison_of_genomes_of_three_persons_in_the_mt_chr.txt"
	EXPECTED="$SCRIPT_DIR/expected/expected_three_genomes_mt_chr.txt"
	
	java -jar $PROJECT_DIR/build/libs/bio_relatives-all-1.3-SNAPSHOT.jar --compare3 $BAM1 $BAM2 $BAM3 $BED -th 4 > $ACTUAL
	sed -i '1,16d' $ACTUAL
	compare_results 3 $EXPECTED $ACTUAL
}

function test_4 {
	echo "Test 4: Comparison of the genomes of three persons in the autosomal chromosome"
	BAM1="$TEST_DIR/resources/genome/compare/testDad4.bam"
	BAM2="$TEST_DIR/resources/genome/compare/testMother4.bam"
	BAM3="$TEST_DIR/resources/genome/compare/testSon4.bam"
	BED="$TEST_DIR/resources/genome/compare/correct.bed"
	
	ACTUAL="$OUTPUT_DIR/test_4.comparison_of_genomes_of_three_persons_in_the_autosomal_chr.txt"
	EXPECTED="$SCRIPT_DIR/expected/expected_three_genomes_autosomal_chr.txt"
	
	java -jar $PROJECT_DIR/build/libs/bio_relatives-all-1.3-SNAPSHOT.jar --compare3 $BAM1 $BAM2 $BAM3 $BED -th 4 > $ACTUAL
	sed -i '1,16d' $ACTUAL
	compare_results 4 $EXPECTED $ACTUAL
}

test_1
test_2
test_3
test_4

	






