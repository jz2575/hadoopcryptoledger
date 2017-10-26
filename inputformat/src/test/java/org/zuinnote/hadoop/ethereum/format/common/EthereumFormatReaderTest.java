/**
* Copyright 2017 ZuInnoTe (Jörn Franke) <zuinnote@gmail.com>
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

package org.zuinnote.hadoop.ethereum.format.common;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;
import org.zuinnote.hadoop.ethereum.format.exception.EthereumBlockReadException;

/**
 * @author jornfranke
 *
 */
public class EthereumFormatReaderTest {
	static final int DEFAULT_BUFFERSIZE=64*1024;
	static final int DEFAULT_MAXSIZE_ETHEREUMBLOCK=1 * 1024 * 1024;
	
	 @Test
	  public void checkTestDataGenesisBlockAvailable() {
		ClassLoader classLoader = getClass().getClassLoader();
		String fileName="ethgenesis.bin";
		String fileNameGenesis=classLoader.getResource("testdata/"+fileName).getFile();	
		assertNotNull("Test Data File \""+fileName+"\" is not null in resource path",fileNameGenesis);
		File file = new File(fileNameGenesis);
		assertTrue("Test Data File \""+fileName+"\" exists", file.exists());
		assertFalse("Test Data File \""+fileName+"\" is not a directory", file.isDirectory());
	  }
	 
	 @Test
	  public void parseGenesisBlockAsEthereumRawBlockHeap() throws IOException, EthereumBlockReadException {
		ClassLoader classLoader = getClass().getClassLoader();
		String fileName="ethgenesis.bin";
		String fileNameBlock=classLoader.getResource("testdata/"+fileName).getFile();	
		File file = new File(fileNameBlock);
		boolean direct=false;
		FileInputStream fin = new FileInputStream(file);
		EthereumBlockReader ebr = null;
		try {
			ebr = new EthereumBlockReader(fin,this.DEFAULT_MAXSIZE_ETHEREUMBLOCK, this.DEFAULT_BUFFERSIZE,direct);
			ByteBuffer blockByteBuffer = ebr.readRawBlock();
			assertFalse("Raw Genesis Block is HeapByteBuffer", blockByteBuffer.isDirect());
			assertEquals("Raw Genesis block has a size of 540 bytes", 540, blockByteBuffer.limit());
		} finally {
			if (ebr!=null) {
				ebr.close();
			}
		}
	  }
	 
	 @Test
	  public void parseGenesisBlockAsEthereumRawBlockDirect() throws IOException, EthereumBlockReadException {
		ClassLoader classLoader = getClass().getClassLoader();
		String fileName="ethgenesis.bin";
		String fileNameBlock=classLoader.getResource("testdata/"+fileName).getFile();	
		File file = new File(fileNameBlock);
		boolean direct=true;
		FileInputStream fin = new FileInputStream(file);
		EthereumBlockReader ebr = null;
		try {
			ebr = new EthereumBlockReader(fin,this.DEFAULT_MAXSIZE_ETHEREUMBLOCK, this.DEFAULT_BUFFERSIZE,direct);
			ByteBuffer blockByteBuffer = ebr.readRawBlock();
			assertTrue("Raw Genesis Block is DirectByteBuffer", blockByteBuffer.isDirect());
			assertEquals("Raw Genesis block has a size of 540 bytes", 540, blockByteBuffer.limit());
		} finally {
			if (ebr!=null) {
				ebr.close();
			}
		}
	  }
	 
	 @Test
	  public void parseGenesisBlockAsEthereumBlockHeap() throws IOException, EthereumBlockReadException, ParseException {
		ClassLoader classLoader = getClass().getClassLoader();
		String fileName="ethgenesis.bin";
		String fileNameBlock=classLoader.getResource("testdata/"+fileName).getFile();	
		File file = new File(fileNameBlock);
		boolean direct=false;
		FileInputStream fin = new FileInputStream(file);
		EthereumBlockReader ebr = null;
		try {
			ebr = new EthereumBlockReader(fin,this.DEFAULT_MAXSIZE_ETHEREUMBLOCK, this.DEFAULT_BUFFERSIZE,direct);
			EthereumBlock eblock = ebr.readBlock();
			EthereumBlockHeader eblockHeader = eblock.getEthereumBlockHeader();
			List<EthereumTransaction> eTransactions = eblock.getEthereumTransactions();
			List<EthereumBlockHeader> eUncles = eblock.getUncleHeaders();
			assertEquals("Genesis block contains 0 transactions", 0, eTransactions.size());
			assertEquals("Genesis block contains 0 uncleHeaders",0, eUncles.size());
			byte[] expectedParentHash = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			assertArrayEquals("Genesis block contains a 32 byte hash consisting only of 0x00", expectedParentHash, eblockHeader.getParentHash());
			byte[] expectedUncleHash = new byte[] {(byte) 0x1D,(byte) 0xCC,0x4D,(byte) 0xE8,(byte) 0xDE, (byte) 0xC7,(byte) 0x5D,
					(byte) 0x7A,(byte) 0xAB,(byte) 0x85,(byte) 0xB5,(byte) 0x67,(byte) 0xB6,(byte) 0xCC,(byte) 0xD4,
					0x1A,(byte) 0xD3,(byte)0x12, 0x45,0x1B,(byte) 0x94,(byte) 0x8A,0x74,0x13,(byte) 0xF0,
					(byte) 0xA1,0x42,(byte) 0xFD,0x40,(byte) 0xD4,(byte) 0x93,0x47};
			assertArrayEquals("Genesis block contains a correct 32 byte uncle hash", expectedUncleHash, eblockHeader.getUncleHash());
			byte[] expectedCoinbase = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			assertArrayEquals("Genesis block contains a 20 byte coinbase consisting only of 0x00",expectedCoinbase,eblockHeader.getCoinBase());
			byte[] expectedStateRoot= new byte[] {(byte) 0xD7,(byte) 0xF8,(byte) 0x97,0x4F,(byte) 0xB5,(byte) 0xAC,0x78,(byte) 0xD9,(byte) 0xAC,0x09,(byte) 0x9B,(byte) 0x9A,(byte) 0xD5,0x01,(byte) 0x8B,(byte) 0xED,(byte) 0xC2,(byte) 0xCE,0x0A,0x72,(byte) 0xDA,(byte) 0xD1,(byte) 0x82,0x7A,0x17,0x09,(byte) 0xDA,0x30,0x58,0x0F,0x05,0x44};
			assertArrayEquals("Genesis block contains a correct 32 byte stateroot",expectedStateRoot,eblockHeader.getStateRoot());
			byte[] expectedTxTrieRoot= new byte[] {0x56,(byte) 0xE8,0x1F,0x17,0x1B,(byte) 0xCC,0x55,(byte) 0xA6,(byte) 0xFF,(byte) 0x83,0x45,(byte) 0xE6,(byte) 0x92,(byte) 0xC0,(byte) 0xF8,0x6E,0x5B,0x48,(byte) 0xE0,0x1B,(byte) 0x99,0x6C,(byte) 0xAD,(byte) 0xC0,0x01,0x62,0x2F,(byte) 0xB5,(byte) 0xE3,0x63,(byte) 0xB4,0x21};
			assertArrayEquals("Genesis block contains a correct 32 byte txTrieRoot",expectedTxTrieRoot,eblockHeader.getTxTrieRoot());	
			byte[] expectedReceiptTrieRoot=new byte[] {0x56,(byte) 0xE8,0x1F,0x17,0x1B,(byte) 0xCC,0x55,(byte) 0xA6,(byte) 0xFF,(byte) 0x83,0x45,(byte) 0xE6,(byte) 0x92,(byte) 0xC0,(byte) 0xF8,0x6E,0x5B,0x48,(byte) 0xE0,0x1B,(byte) 0x99,0x6C,(byte) 0xAD,(byte) 0xC0,0x01,0x62,0x2F,(byte) 0xB5,(byte) 0xE3,0x63,(byte) 0xB4,0x21};
			assertArrayEquals("Genesis block contains a correct 32 byte ReceiptTrieRoot",expectedReceiptTrieRoot,eblockHeader.getReceiptTrieRoot());
			byte[] expectedLogsBloom = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			assertArrayEquals("Genesis block contains a 256 byte log bloom consisting only of 0x00", expectedLogsBloom, eblockHeader.getLogsBloom());
			byte[] expectedDifficulty = new byte[] {0x04,0x00,0x00,0x00,0x00};
			assertArrayEquals("Genesis block contains a correct 5 byte difficulty", expectedDifficulty, eblockHeader.getDifficulty());
			//DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
			//long expectedTimestamp = format.parse("30-07-2015 03:26:13 UTC").getTime();
			assertEquals("Genesis block contains a timestamp of 0",0L, eblockHeader.getTimestamp());
			long expectedNumber = 0L;
			assertEquals("Genesis block contains a number 0", expectedNumber, eblockHeader.getNumber());
			byte[] expectedGasLimit = new byte[] {0x13,(byte) 0x88};
			assertArrayEquals("Genesis block contains a correct 2 byte gas limit", expectedGasLimit, eblockHeader.getGasLimit());
			long expectedGasUsed = 0L;
			assertEquals("Genesis block contains a gas used of  0", expectedGasUsed, eblockHeader.getGasUsed());
			byte[] expectedMixHash= new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			assertArrayEquals("Genesis block contains a correct 32 byte mix hash consiting only of 0x00", expectedMixHash, eblockHeader.getMixHash());
			byte[] expectedExtraData= new byte[] {0x11,(byte) 0xBB,(byte) 0xE8,(byte) 0xDB,0x4E,0x34,0x7B,0x4E,(byte) 0x8C,(byte) 0x93,0x7C,0x1C,(byte) 0x83,0x70,(byte) 0xE4,(byte) 0xB5,(byte) 0xED,0x33,(byte) 0xAD,(byte) 0xB3,(byte) 0xDB,0x69,(byte) 0xCB,(byte) 0xDB,0x7A,0x38,(byte) 0xE1,(byte) 0xE5,0x0B,0x1B,(byte) 0x82,(byte) 0xFA};
			assertArrayEquals("Genesis block contains correct 32 byte extra data", expectedExtraData, eblockHeader.getExtraData());
			byte[] expectedNonce = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x42};
			assertArrayEquals("Genesis block contains a correct 8 byte nonce", expectedNonce, eblockHeader.getNonce());
		} finally {
			if (ebr!=null) {
				ebr.close();
			}
		}
	  }
	 
	 
	 @Test
	  public void parseGenesisBlockAsEthereumBlockDirect() throws IOException, EthereumBlockReadException, ParseException {
		ClassLoader classLoader = getClass().getClassLoader();
		String fileName="ethgenesis.bin";
		String fileNameBlock=classLoader.getResource("testdata/"+fileName).getFile();	
		File file = new File(fileNameBlock);
		boolean direct=true;
		FileInputStream fin = new FileInputStream(file);
		EthereumBlockReader ebr = null;
		try {
			ebr = new EthereumBlockReader(fin,this.DEFAULT_MAXSIZE_ETHEREUMBLOCK, this.DEFAULT_BUFFERSIZE,direct);
			EthereumBlock eblock = ebr.readBlock();
			EthereumBlockHeader eblockHeader = eblock.getEthereumBlockHeader();
			List<EthereumTransaction> eTransactions = eblock.getEthereumTransactions();
			List<EthereumBlockHeader> eUncles = eblock.getUncleHeaders();
			assertEquals("Genesis block contains 0 transactions", 0, eTransactions.size());
			assertEquals("Genesis block contains 0 uncleHeaders",0, eUncles.size());
			byte[] expectedParentHash = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			assertArrayEquals("Genesis block contains a 32 byte hash consisting only of 0x00", expectedParentHash, eblockHeader.getParentHash());
			byte[] expectedUncleHash = new byte[] {(byte) 0x1D,(byte) 0xCC,0x4D,(byte) 0xE8,(byte) 0xDE, (byte) 0xC7,(byte) 0x5D,
					(byte) 0x7A,(byte) 0xAB,(byte) 0x85,(byte) 0xB5,(byte) 0x67,(byte) 0xB6,(byte) 0xCC,(byte) 0xD4,
					0x1A,(byte) 0xD3,(byte)0x12, 0x45,0x1B,(byte) 0x94,(byte) 0x8A,0x74,0x13,(byte) 0xF0,
					(byte) 0xA1,0x42,(byte) 0xFD,0x40,(byte) 0xD4,(byte) 0x93,0x47};
			assertArrayEquals("Genesis block contains a correct 32 byte uncle hash", expectedUncleHash, eblockHeader.getUncleHash());
			byte[] expectedCoinbase = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			assertArrayEquals("Genesis block contains a 20 byte coinbase consisting only of 0x00",expectedCoinbase,eblockHeader.getCoinBase());
			byte[] expectedStateRoot= new byte[] {(byte) 0xD7,(byte) 0xF8,(byte) 0x97,0x4F,(byte) 0xB5,(byte) 0xAC,0x78,(byte) 0xD9,(byte) 0xAC,0x09,(byte) 0x9B,(byte) 0x9A,(byte) 0xD5,0x01,(byte) 0x8B,(byte) 0xED,(byte) 0xC2,(byte) 0xCE,0x0A,0x72,(byte) 0xDA,(byte) 0xD1,(byte) 0x82,0x7A,0x17,0x09,(byte) 0xDA,0x30,0x58,0x0F,0x05,0x44};
			assertArrayEquals("Genesis block contains a correct 32 byte stateroot",expectedStateRoot,eblockHeader.getStateRoot());
			byte[] expectedTxTrieRoot= new byte[] {0x56,(byte) 0xE8,0x1F,0x17,0x1B,(byte) 0xCC,0x55,(byte) 0xA6,(byte) 0xFF,(byte) 0x83,0x45,(byte) 0xE6,(byte) 0x92,(byte) 0xC0,(byte) 0xF8,0x6E,0x5B,0x48,(byte) 0xE0,0x1B,(byte) 0x99,0x6C,(byte) 0xAD,(byte) 0xC0,0x01,0x62,0x2F,(byte) 0xB5,(byte) 0xE3,0x63,(byte) 0xB4,0x21};
			assertArrayEquals("Genesis block contains a correct 32 byte txTrieRoot",expectedTxTrieRoot,eblockHeader.getTxTrieRoot());	
			byte[] expectedReceiptTrieRoot=new byte[] {0x56,(byte) 0xE8,0x1F,0x17,0x1B,(byte) 0xCC,0x55,(byte) 0xA6,(byte) 0xFF,(byte) 0x83,0x45,(byte) 0xE6,(byte) 0x92,(byte) 0xC0,(byte) 0xF8,0x6E,0x5B,0x48,(byte) 0xE0,0x1B,(byte) 0x99,0x6C,(byte) 0xAD,(byte) 0xC0,0x01,0x62,0x2F,(byte) 0xB5,(byte) 0xE3,0x63,(byte) 0xB4,0x21};
			assertArrayEquals("Genesis block contains a correct 32 byte ReceiptTrieRoot",expectedReceiptTrieRoot,eblockHeader.getReceiptTrieRoot());
			byte[] expectedLogsBloom = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			assertArrayEquals("Genesis block contains a 256 byte log bloom consisting only of 0x00", expectedLogsBloom, eblockHeader.getLogsBloom());
			byte[] expectedDifficulty = new byte[] {0x04,0x00,0x00,0x00,0x00};
			assertArrayEquals("Genesis block contains a correct 5 byte difficulty", expectedDifficulty, eblockHeader.getDifficulty());
			//DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
			//long expectedTimestamp = format.parse("30-07-2015 03:26:13 UTC").getTime();
			assertEquals("Genesis block contains a timestamp of 0",0L, eblockHeader.getTimestamp());
			long expectedNumber = 0L;
			assertEquals("Genesis block contains a number 0", expectedNumber, eblockHeader.getNumber());
			byte[] expectedGasLimit = new byte[] {0x13,(byte) 0x88};
			assertArrayEquals("Genesis block contains a correct 2 byte gas limit", expectedGasLimit, eblockHeader.getGasLimit());
			long expectedGasUsed = 0L;
			assertEquals("Genesis block contains a gas used of  0", expectedGasUsed, eblockHeader.getGasUsed());
			byte[] expectedMixHash= new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
			assertArrayEquals("Genesis block contains a correct 32 byte mix hash consiting only of 0x00", expectedMixHash, eblockHeader.getMixHash());
			byte[] expectedExtraData= new byte[] {0x11,(byte) 0xBB,(byte) 0xE8,(byte) 0xDB,0x4E,0x34,0x7B,0x4E,(byte) 0x8C,(byte) 0x93,0x7C,0x1C,(byte) 0x83,0x70,(byte) 0xE4,(byte) 0xB5,(byte) 0xED,0x33,(byte) 0xAD,(byte) 0xB3,(byte) 0xDB,0x69,(byte) 0xCB,(byte) 0xDB,0x7A,0x38,(byte) 0xE1,(byte) 0xE5,0x0B,0x1B,(byte) 0x82,(byte) 0xFA};
			assertArrayEquals("Genesis block contains correct 32 byte extra data", expectedExtraData, eblockHeader.getExtraData());
			byte[] expectedNonce = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x42};
			assertArrayEquals("Genesis block contains a correct 8 byte nonce", expectedNonce, eblockHeader.getNonce());
		} finally {
			if (ebr!=null) {
				ebr.close();
			}
		}
	  }

}
