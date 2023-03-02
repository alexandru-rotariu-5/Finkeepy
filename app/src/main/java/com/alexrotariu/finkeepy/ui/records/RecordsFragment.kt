package com.alexrotariu.finkeepy.ui.records

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexrotariu.finkeepy.App
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.databinding.FragmentDashboardBinding
import com.alexrotariu.finkeepy.databinding.FragmentRecordsBinding
import com.alexrotariu.finkeepy.ui.RecordAdapter
import com.alexrotariu.finkeepy.ui.dashboard.DashboardViewModel
import com.alexrotariu.finkeepy.ui.dashboard.RecordItemDecoration
import com.alexrotariu.finkeepy.utils.format
import com.alexrotariu.finkeepy.utils.split
import javax.inject.Inject

class RecordsFragment : Fragment() {

    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModel: RecordsViewModel
    private lateinit var recordAdapter: RecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordsBinding.inflate(inflater, container, false)
        (activity?.application as App).appComponent.inject(this)
        initRecordsAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
    }

    private fun initRecordsAdapter() {
        recordAdapter = RecordAdapter()
        binding.rvRecords.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recordAdapter
        }

        binding.rvRecords.addItemDecoration(RecordItemDecoration(resources.getDimensionPixelSize(R.dimen.record_item_vertical_space)))
    }

    private fun initObservers() {
        viewModel.records.observe(viewLifecycleOwner) { records ->
            if (records != null) {
                updateRecords(records)
            }
        }
    }

    private fun updateRecords(records: List<Record?>?) {
        recordAdapter.setFullList(records)
    }
}