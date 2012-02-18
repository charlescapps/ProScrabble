#!/bin/bash

URPROGRAM=$1

for i in {1..10}
do
    cat "testcases/test$i.txt" | $URPROGRAM | java -jar arbitrate.jar data/layout.txt dict/OSPDv2.txt "testcases/test$i.txt"
done
