package masaya.release.manage_menu

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import masaya.release.manage_menu.imageFile.ImageFiles
import masaya.release.manage_menu.databinding.FragmentImageBinding
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.MotionEvent
import android.graphics.Matrix
import android.widget.ImageView


class FragmentImage : Fragment() {

    private val navigationArgs: FragmentImageArgs by navArgs()

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // メニューのために必要
        setHasOptionsMenu(true)

    }

    // アプリケーションバーのオプション用メニューを生成
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // 左上の←（戻るボタン）を表示
        val activity = activity as AppCompatActivity?
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 修正対象のID
        val bmpName = navigationArgs.bmpName
        // 内部ストレージへ書き込んだファイルから改めて画像取得
        val bitmap = ImageFiles.readImgsFromFileName(requireActivity(), bmpName)

        binding.foodimage.setImageBitmap(bitmap)
    }
}

