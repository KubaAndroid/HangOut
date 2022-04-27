package jw.adamiak.hangout.ui.start.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import jw.adamiak.hangout.R
import jw.adamiak.hangout.databinding.FragmentAboutBinding

class AboutFragment: Fragment(R.layout.fragment_about) {
	private lateinit var binding: FragmentAboutBinding
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentAboutBinding.bind(view)
		binding.btnAboutBack.setOnClickListener {
			findNavController().popBackStack()
		}
	}
}