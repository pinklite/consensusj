#!/bin/bash
set -x

function cleanup {
    kill -15 $BTCPID
}
trap cleanup EXIT

# We are currently using Omni Core since it a superset of Bitcoin Core
BITCOIND=copied-artifacts/src/omnicored
DATADIR=$HOME/bitcoin-data-dir
LOGDIR=logs
OMNILOG=/tmp/omnicore.log

# Assume bitcoind built elsewhere and copied by without x permission
chmod +x $BITCOIND

# Setup bitcoin conf and data dir
mkdir -p $DATADIR
cp -n bitcoin.conf $DATADIR

# setup logging
mkdir -p $LOGDIR
touch $OMNILOG
ln -sf $OMNILOG $LOGDIR/omnicore.log

# Remove all regtest data
rm -rf $DATADIR/regtest

# Run bitcoind in regtest mode
$BITCOIND -regtest -datadir=$DATADIR -paytxfee=0.0001 -minrelaytxfee=0.00001 -limitancestorcount=750 -limitdescendantcount=750 -rpcserialversion=0 > $LOGDIR/bitcoin.log &
BTCSTATUS=$?
BTCPID=$!

# Give server some time to start
# sleep 30

# Run integration tests
echo "Running Bitcoin Core RPC integration tests in RegTest mode..."
./gradlew regTest
GRADLESTATUS=$?

exit $GRADLESTATUS
