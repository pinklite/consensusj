package com.msgilligan.bitcoinj

import com.msgilligan.bitcoinj.rpc.BitcoinExtendedClient
import org.consensusj.jsonrpc.groovy.Loggable
import com.msgilligan.bitcoinj.rpc.RpcURI
import com.msgilligan.bitcoinj.test.BTCTestSupport
import com.msgilligan.bitcoinj.test.RegTestFundingSource
import org.bitcoinj.core.Coin
import spock.lang.Specification
import com.msgilligan.bitcoinj.rpc.test.TestServers


/**
 * Abstract Base class for Spock tests of Bitcoin Core in RegTest mode
 */
abstract class BaseRegTestSpec extends Specification implements BTCTestSupport, Loggable {
    static final Coin minBTCForTests = 50.btc
    static final private TestServers testServers = TestServers.instance
    static final protected String rpcTestUser = testServers.rpcTestUser
    static final protected String rpcTestPassword = testServers.rpcTestPassword;

    // Initializer to set up trait properties, Since Spock doesn't allow constructors
    {
        client = new BitcoinExtendedClient(RpcURI.defaultRegTestURI, rpcTestUser, rpcTestPassword)
        fundingSource = new RegTestFundingSource(client)
    }

    void setupSpec() {
        serverReady()

        // Make sure we have enough test coins
        // Do we really need to keep doing this now that most tests
        // explicitly fund their addresses?
        while (client.getBalance() < minBTCForTests) {
            // Mine blocks until we have some coins to spend
            client.generateBlocks(1)
        }
    }

    /**
     * Clean up after all tests in spec have run.
     */
    void cleanupSpec() {
        // Spend almost all coins as fee, to sweep dust
        consolidateCoins()
    }

}