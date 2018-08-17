let isVisible = true;

const toggleItemVisibility = (list) => {
  document.querySelector("." + list + ">.list-of-features").style.maxHeight = (isVisible ? "0px" : "700px");
  document.querySelector('.' + list + ">div>.arrow-before-title").style.transform = (isVisible ? "rotate(90deg)" : "rotate(0deg)");
  isVisible = !isVisible;
}

export default toggleItemVisibility;