package com.alexrotariu.finkeepy.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexrotariu.finkeepy.App
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.databinding.FragmentDashboardBinding
import com.alexrotariu.finkeepy.utils.split
import com.alexrotariu.finkeepy.utils.format
import javax.inject.Inject

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModel: DashboardViewModel
    private lateinit var recordAdapter: RecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        (activity?.application as App).appComponent.inject(this)
        initRecordsAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
    }

    private fun initRecordsAdapter() {
        recordAdapter = RecordAdapter(3)
        binding.rvRecords.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recordAdapter
        }

        binding.rvRecords.addItemDecoration(RecordItemDecoration(resources.getDimensionPixelSize(R.dimen.record_item_vertical_space)))
    }

    private fun initObservers() {
        viewModel.records.observe(viewLifecycleOwner) { records ->
            if (records != null) {
                updateNetWorthView(viewModel.getNetWorth())
                updateLastMonthCashflowView(viewModel.getLastMonthCashflow())
                updateRecords(records)
            }
        }
    }

    private fun updateNetWorthView(netWorth: Double) {
        binding.tvNetWorthWhole.text = netWorth.split().first.format()
        binding.tvNetWorthDecimal.text =
            String.format(getString(R.string.decimal), netWorth.split().second.toString())
    }

    private fun updateLastMonthCashflowView(cashflow: Double) {
        binding.tvLastMonthCashflow.text = cashflow.split().first.format()
    }

    private fun updateRecords(records: List<Record?>?) {
        recordAdapter.setFullList(records)
    }
}