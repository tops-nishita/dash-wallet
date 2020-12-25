/*
 * Copyright 2020 Dash Core Group.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.schildbach.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.multidex.MultiDexApplication
import de.schildbach.wallet.rates.ExchangeRatesRepository
import org.bitcoinj.core.Address
import org.bitcoinj.wallet.Wallet
import org.dash.wallet.common.WalletDataProvider
import org.dash.wallet.common.data.ExchangeRate

abstract class BaseWalletApplication : MultiDexApplication(), WalletDataProvider {
    protected abstract val wallet: Wallet?
    override fun freshReceiveAddress(): Address {
        return if (wallet != null) {
            wallet!!.freshReceiveAddress()
        } else {
            throw RuntimeException("this method cant't be used before creating the wallet")
        }
    }

    override fun getExchangeRate(currencyCode: String): LiveData<ExchangeRate> {
        return ExchangeRatesRepository.getInstance().getRate(currencyCode).switchMap {
            return@switchMap liveData {
                emit(ExchangeRate(it.currencyCode, it.rate, it.getCurrencyName(this@BaseWalletApplication), it.fiat))
            }
        }
    }
}