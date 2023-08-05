/*
 * Designed and developed by 2020 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skydoves.sandwichdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.sandwichdemo.databinding.ItemPosterBinding
import com.skydoves.sandwichdemo.model.Poster

class PosterAdapter : RecyclerView.Adapter<PosterAdapter.PosterViewHolder>() {

  private val items = mutableListOf<Poster>()

  fun addPosterList(posters: List<Poster>) {
    items.clear()
    items.addAll(posters)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
    val binding = ItemPosterBinding.inflate(LayoutInflater.from(parent.context))
    return PosterViewHolder(binding)
  }

  override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
    val item = items[position]
    with(holder.binding) {
      poster = item
      ViewCompat.setTransitionName(itemContainer, item.name)
      executePendingBindings()
    }
  }

  override fun getItemCount(): Int = items.size

  class PosterViewHolder(
    val binding: ItemPosterBinding,
  ) : RecyclerView.ViewHolder(binding.root)
}
