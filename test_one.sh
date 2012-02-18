#!/bin/bash

URPROGRAM=$1
TESTFILE=$2

    cat $TESTFILE | $URPROGRAM | java -jar arbitrate.jar data/layout.txt dict/OSPDv2.txt $TESTFILE
