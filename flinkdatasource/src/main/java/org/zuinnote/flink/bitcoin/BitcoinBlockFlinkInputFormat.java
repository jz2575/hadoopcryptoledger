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

/**
 * Flink Data Source for the Bitcoin Block format
 */

package org.zuinnote.flink.bitcoin;

import java.io.IOException;

import org.zuinnote.hadoop.bitcoin.format.common.BitcoinBlock;
import org.zuinnote.hadoop.bitcoin.format.exception.BitcoinBlockReadException;
import org.zuinnote.hadoop.bitcoin.format.exception.HadoopCryptoLedgerConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;


public class BitcoinBlockFlinkInputFormat extends AbstractBitcoinFlinkInputFormat<BitcoinBlock> {
	


	private static final Log LOG = LogFactory.getLog(BitcoinBlockFlinkInputFormat.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 4150883073922261077L;
	private boolean isEndReached;
	
	public BitcoinBlockFlinkInputFormat(int maxSizeBitcoinBlock, int bufferSize, String specificMagicStr,
			boolean useDirectBuffer) throws HadoopCryptoLedgerConfigurationException {
		super(maxSizeBitcoinBlock, bufferSize, specificMagicStr, useDirectBuffer);
		this.isEndReached=false;
	}
	
	@Override
	public boolean reachedEnd() throws IOException {
		return this.isEndReached;
	}

	@Override
	public BitcoinBlock nextRecord(BitcoinBlock reuse) throws IOException {
		BitcoinBlock dataBlock=null;
		if (this.stream.getPos()<=this.currentSplit.getStart()+this.currentSplit.getLength()) {
			try {
				dataBlock=this.getBbr().readBlock();
			} catch(BitcoinBlockReadException e) {
				LOG.error(e);
			}
			if (dataBlock==null) {
				this.isEndReached=true;
			}
		} else {
			this.isEndReached=true;
		}
		return dataBlock;
	}
	
}