package com.alexrotariu.finkeepy.ui.main.records

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexrotariu.finkeepy.R
import com.alexrotariu.finkeepy.data.models.Record
import com.alexrotariu.finkeepy.databinding.FragmentRecordsBinding
import com.alexrotariu.finkeepy.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_records.view.nsvRecords

class RecordsFragment : Fragment() {

    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var mainActivity: MainActivity

    private lateinit var recordAdapter: RecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMainActivity()
        initRecordsAdapter()
        initObservers()
        initScrollListener()
        setupMainHeaderTitle()
    }

    private fun initMainActivity() {
        mainActivity = activity as MainActivity
    }

    private fun getMainViewModel() = mainActivity.viewModel

    private fun initRecordsAdapter() {
        recordAdapter = RecordAdapter(fragmentManager = childFragmentManager)
        binding.rvRecords.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recordAdapter
        }
    }

    private fun initScrollListener() {
        binding.root.nsvRecords.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            updateMainHeaderElevation(scrollY)
            updateMainHeaderTitleY(scrollY)
        }
    }

    private fun updateMainHeaderElevation(scrollY: Int) {
        val mainHeaderElevation = when (scrollY) {
            in 0..80 -> (scrollY / 2).toFloat()
            else -> 40f
        }

        mainActivity.setMainHeaderElevation(mainHeaderElevation)
    }

    private fun updateMainHeaderTitleY(scrollY: Int) {
        val mainHeaderTitleY = when {
            scrollY in 70..120 -> 130f - (2.6f * (scrollY - 70))
            scrollY > 120 -> 0f
            else -> 130f
        }

        mainActivity.setMainHeaderTitleY(mainHeaderTitleY)
    }


    private fun setupMainHeaderTitle() {
        mainActivity.apply {
            setMainHeaderTitle(getString(R.string.records))
            setMainHeaderTitleY(130f)
            showMainHeaderTitle()
        }
    }

    private fun initObservers() {
        getMainViewModel().records.observe(viewLifecycleOwner) { records ->
            if (records != null) {
                updateRecords(records)
            }
        }
    }

    private fun updateRecords(records: List<Record?>?) {
        recordAdapter.setFullList(records)
    }
    
    override fun onDestroy() {
        mainActivity.hideMainHeaderTitle()
        mainActivity.setMainHeaderElevation(0f)
        super.onDestroy()
    }
}