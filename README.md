# ExpandableViewHolder
ExpandableViewHolder
使用说明：
1.自建的viewhoder 需要继承 ExpandableViewHoldersUtil.Expandable
实现 
@Override
        public View getExpandView() {
            return lvLinearlayout;
        }

view 不能为空，
初始设置：
lvLinearlayout.setVisibility(View.GONE);
lvLinearlayout.setAlpha(0);

doCustomAnim 用于处理和展开相关的动画，可不处理

2.Adapter 里面 onBindViewHolder
需要 keepOne.bind(viewHolder, position);


3.点击展示的事件是 
keepOne.toggle(viewHolder);

4.其他请参照代码
