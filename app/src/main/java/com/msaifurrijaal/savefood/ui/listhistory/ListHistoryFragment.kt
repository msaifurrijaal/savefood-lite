package com.msaifurrijaal.savefood.ui.listhistory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.adapter.HistoryAdapter
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Transaction
import com.msaifurrijaal.savefood.databinding.FragmentListHistoryBinding
import com.msaifurrijaal.savefood.ui.detailproduct.DetailProductActivity
import com.msaifurrijaal.savefood.ui.detailtransaction.DetailTransactionActivity
import com.msaifurrijaal.savefood.ui.detailtransaction.DetailTransactionActivity.Companion.TRANSACTION_ITEM
import com.msaifurrijaal.savefood.utils.showDialogError


class ListHistoryFragment : Fragment() {

    private var _binding: FragmentListHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyTransactionRv : HistoryAdapter
    private lateinit var listHistoryViewModel: ListHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListHistoryBinding.inflate(inflater, container, false)

        listHistoryViewModel = ViewModelProvider(this).get(ListHistoryViewModel::class.java)
        historyTransactionRv = HistoryAdapter(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observerHistoryData()
        onBtnMapsClick()
        onItemTransactionClick()
    }

    private fun onItemTransactionClick() {
        historyTransactionRv.onItemClick = { transaction ->
            startActivity(
                Intent(activity, DetailTransactionActivity::class.java)
                    .putExtra(TRANSACTION_ITEM, transaction))
        }
    }

    private fun onBtnMapsClick() {
        historyTransactionRv.onBtnMapsClick = { transaction ->
            val gmmIntentUri = Uri.parse("google.navigation:q=${transaction!!.latitude},${transaction!!.longitude}")

            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                showDialogError(requireContext(), getString(R.string.please_install_gmaps_first))
            }
        }
    }

    private fun observerHistoryData() {
        val slidePosition = arguments?.getInt("slidePosition", 0) ?: 0

        when (slidePosition) {
            0 -> {
                listHistoryViewModel.getListTransaction().observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Error -> {
                            actionError()
                            showDialogError(
                                requireContext(),
                                response.message.toString()
                            )
                        }
                        is Resource.Loading -> {
                            actionLoading()
                        }
                        is Resource.Success -> {
                            setHistoryRv(response.data)
                            actionSucces()
                        }
                    }
                })
            }
            1 -> {
                listHistoryViewModel.getListTransactionFilter("receiver").observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Error -> {
                            actionError()
                            showDialogError(
                                requireContext(),
                                response.message.toString()
                            )
                        }
                        is Resource.Loading -> {
                            actionLoading()
                        }
                        is Resource.Success -> {
                            setHistoryRv(response.data)
                            actionSucces()
                        }
                    }
                })
            }
            2 -> {
                listHistoryViewModel.getListTransactionFilter("sender").observe(viewLifecycleOwner, Observer { response ->
                    when (response) {
                        is Resource.Error -> {
                            actionError()
                            showDialogError(
                                requireContext(),
                                response.message.toString()
                            )
                        }
                        is Resource.Loading -> {
                            actionLoading()
                        }
                        is Resource.Success -> {
                            setHistoryRv(response.data)
                            actionSucces()
                        }
                    }
                })
            }
        }
    }

    private fun setHistoryRv(data: List<Transaction>?) {
        data?.let {
            historyTransactionRv.setHistoryList(data)
            if (data.size == 0) {
                actionEmpty()
            } else {
                actionNoEmpty()
            }
        }

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = historyTransactionRv
        }
    }

    private fun actionLoading() {
        binding.apply {
            pgListHistory.visibility = View.VISIBLE
            rvHistory.visibility = View.INVISIBLE
        }
    }

    private fun actionEmpty() {
        binding.apply {
            pgListHistory.visibility = View.GONE
            rvHistory.visibility = View.VISIBLE
            tvTransactionEmpty.visibility = View.VISIBLE
        }
    }

    private fun actionNoEmpty() {
        binding.apply {
            pgListHistory.visibility = View.GONE
            rvHistory.visibility = View.VISIBLE
            tvTransactionEmpty.visibility = View.GONE
        }
    }

    private fun actionSucces() {
        binding.apply {
            pgListHistory.visibility = View.GONE
            rvHistory.visibility = View.VISIBLE
        }
    }

    private fun actionError() {
        binding.apply {
            pgListHistory.visibility = View.GONE
            rvHistory.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}