package masaya.release.manage_menu

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import masaya.release.manage_menu.databinding.FragmentImageBinding

class ImageFragment : Fragment(){

    private val navigationArgs: ImageFragmentArgs by navArgs()

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
        // TODO これ引数が戻せない
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
        val loadbmp = readImgsFromFileName(bmpName, requireActivity())
        binding.foodimage.setImageBitmap(loadbmp)

    }
}