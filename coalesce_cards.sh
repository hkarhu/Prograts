files=""; for i in $(seq -w $1 $2); do files="$files ar-cards/$i.png"; done
montage -geometry +0+0 -coalesce $files "combined_$1_$2.png"
